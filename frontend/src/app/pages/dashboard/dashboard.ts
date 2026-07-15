import { Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Goal } from '../../types/Goal';
import { Contribution } from '../../types/Contribution';
import { GoalService } from '../../services/GoalService';
import { UserService } from '../../services/UserService';
import { SideBar } from '../../components/side-bar/side-bar';
import { currencyPipe } from '../../pipes/currency-pipe';
import { ContributionService } from '../../services/ContributionService';
import { FundingSourceService } from '../../services/FundingSourceSevice';
import { mergeMap, range, toArray } from 'rxjs';
@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, SideBar, currencyPipe],
  templateUrl: './dashboard.html',
  styleUrl: '../utils/css/dashboard/styles.css',
})
export class Dashboard {
  // data we load
  userName = signal<string>(''); // Welcome back {username}
  goals = signal<Goal[]>([]); //user's goals
  loading = signal<boolean>(false);
  contributions = signal<Contribution[]>([]);
  fundingSourcesCount = signal<number>(0);

  contributionCount = computed(() => this.contributions().length);

  // total target across all goals
  totalTarget = computed(() => this.goals().reduce((sum, g) => sum + Number(g.targetAmount), 0));
  
  totalContributed = computed(() => this.contributions().reduce((sum, c) => sum + Number(c.amount), 0));
  stillToGo = computed(() => Math.max(this.totalTarget() - this.totalContributed(), 0));

  percentFunded = computed(() => {
    const target = this.totalTarget();
    if (target === 0) return 0;
    return Math.round((this.totalContributed()/target) * 100);
  });

  // get current year
  currentYear = new Date().getFullYear();
  contributedThisYear = computed(() => this.contributions().filter((c) => new Date(c.date).getFullYear() === this.currentYear)
                                        .reduce((sum, c) => sum + Number(c.amount), 0));
  
                                        // get top goals
  topGoals = computed(() => this.goals().slice(0,3));

  constructor(
    private goalService: GoalService,
    private userService: UserService,
    private contributionService: ContributionService,
    private sourceService: FundingSourceService
  ){}

  ngOnInit(): void {
    this.loading.set(true);

    // load username
    this.userService.getCurrentUser().subscribe({
      next: (user) => this.userName.set(user.username),
      error: () => this.userName.set('') //fallback empty string
    });

    // load the goals
    this.goalService.getGoalsPage(0).subscribe({
      next: (data) => 
        this.goals.set(data.content),
        error: () => this.goals.set([]),
    });

    // funding sources count
    this.sourceService.getSources(0).subscribe({
      next: (data) =>
        this.fundingSourcesCount.set(data.totalElements),
        error: () => this.fundingSourcesCount.set(0),
    });
    this.loadAllContributions();
  }

  private loadAllContributions(): void {
    this.contributionService.getContributions(0).subscribe({
      next: (first) => {
        if (first.totalPages <= 1) {
          this.contributions.set(first.content);
          this.loading.set(false);
          return;
        }
        // fetch pages 1..totalPages-1 and append them to page 0
        const rest = range(1, first.totalPages - 1).pipe(
          mergeMap((p) => this.contributionService.getContributions(p)),
          toArray()
        );
        rest.subscribe({
          next: (pages) => {
            const all = [...first.content, ...pages.flatMap((p) => p.content)];
            this.contributions.set(all);
            this.loading.set(false);
          },
          error: () => {
            this.contributions.set(first.content);
            this.loading.set(false);
          },
        });
      },
      error: () => {
        this.contributions.set([]);
        this.loading.set(false);
      },
    });
  }

  // helper functions

  goalContributed(goal: Goal): number {
    return this.contributions().filter((c) => c.goalId === goal.id).reduce((sum, c) => sum + Number(c.amount), 0);
  }

  // % funded for a single goal (placeholder: same 33% idea, per goal)
  goalPercent(goal: Goal): number {
    const target = Number(goal.targetAmount);
    if (target === 0) return 0;
    return Math.round((this.goalContributed(goal)/target) * 100);
  }

  // a status label from the percent
  goalStatus(percent: number): string {
    if (percent >= 100) return 'Achieved';
    if (percent >= 40) return 'On Track';
    return 'At Risk';
  }

  barWidth(percent: number): number{
    return Math.min(percent,100);
  }
}