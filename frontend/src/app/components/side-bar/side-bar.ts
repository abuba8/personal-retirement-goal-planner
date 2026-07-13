import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/AuthService';
import { UserService } from '../../services/UserService';

@Component({
  selector: 'app-side-bar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './side-bar.html',
  styleUrl: './side-bar.css',
})
export class SideBar {
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private router = inject(Router);

  userName = signal<string>('');

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => this.userName.set(user.username),
      error: () => this.userName.set(''),
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
