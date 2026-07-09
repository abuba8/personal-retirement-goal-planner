import { Component, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { MenubarModule } from 'primeng/menubar';
import { Toast } from 'primeng/toast';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MenubarModule, Toast],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('Personal Retirement Goal Planner');

  constructor(
    private router: Router
  ) {}

  navItems: MenuItem[] = [
    {label: "Funding Sources", command: () => this.router.navigate(["/sources"])},
    {label: "Contributions", command: () => this.router.navigate(["/contributions"])},
  ]
}
