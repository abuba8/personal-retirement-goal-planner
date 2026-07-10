import { Component, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { MenubarModule } from 'primeng/menubar';
import { Toast } from 'primeng/toast';
import { AuthService } from './services/AuthService';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MenubarModule, Toast],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('Personal Retirement Goal Planner');

  constructor(
    private router: Router,
    private authService: AuthService,
  ) {}

  navItems: MenuItem[] = [
    {label: "Goals", command: () => this.router.navigate(["/goals"])},
    {label: "Funding Sources", command: () => this.router.navigate(["/sources"])},
    {label: "Contributions", command: () => this.router.navigate(["/contributions"])},
    {label: "Log Out", command: () => {this.router.navigate(["/login"]); this.authService.logout();}}
  ]
}
