import { Component, computed, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Goal } from '../../types/Goal';
import { AuthService } from '../../services/AuthService';
import { GoalService } from '../../services/GoalService';
import { UserService } from '../../services/UserService';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: '../utils/css/dashboard/styles.css',
})
export class Dashboard {
  // data we load
  userName = signal<string>(''); // Welcome back {username}
  goals = signal<Goal[]>([]); //user's goals
  loading = signal<boolean>(false);

  // TODO: replace when a endpoint exists for these
  fundingSourcesCount = signal<number>(4);
  contributionCount = signal<number>(16);

  // total target across all goals
  totalTarget = computed(() => this.goals().reduce((sum, g) => sum + Number(g.targetAmount), 0));
  
  // TODO: replace with real total once contributions are tracked in backend
  totalContributed = computed(() => Math.round(this.totalTarget() * 0.33));
  stillToGo = computed(() => this.totalTarget() - this.totalContributed());

  percentFunded = computed(() => {
    const target = this.totalTarget();
    if (target === 0) return 0;
    return Math.round((this.totalContributed()/target) * 100);
  });

  // get current year
  currentYear = new Date().getFullYear();

  // get top goals
  topGoals = computed(() => this.goals().slice(0,3));

  constructor(
    private goalService: GoalService,
    private userService: UserService,
    private authService: AuthService,
    private router: Router
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
      next: (data) => {
        this.goals.set(data.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false), 
    });
  }

  // helper functions

  // % funded for a single goal (placeholder: same 33% idea, per goal)
  goalPercent(goal: Goal): number {
    const target = Number(goal.targetAmount);
    if (target === 0) return 0;
    return Math.round((target * 0.33 / target) * 100); // = 33; simple placeholder
  }

  // a status label from the percent
  goalStatus(percent: number): string {
    if (percent >= 100) return 'Achieved';
    if (percent >= 40) return 'On Track';
    return 'At Risk';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}