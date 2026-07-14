import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../../services/UserService';
import { UpdateProfile } from '../../types/UserProfile';
import { AuthService } from '../../services/AuthService';
import { SideBar } from '../../components/side-bar/side-bar';

@Component({
  selector: 'app-profile',
  imports: [ReactiveFormsModule, RouterLink, SideBar],
  templateUrl: './profile.html',
  styleUrls: [
    '../utils/css/dashboard/styles.css',
    './profile.css'
  ]
})
export class Profile {

  form!: FormGroup;
  loading = signal<boolean>(false);
  info = signal<string | null>(null);
  error = signal<string | null>(null);
  userName = signal<string>(''); // Welcome back {username}
  userEmail = signal<string>('');

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.form.patchValue({username: user.username, email: user.email});
        this.userName.set(user.username);
        this.userEmail.set(user.email);
      },
      error: () => this.error.set("Could not load profile"),
    });

    this.form = this.formBuilder.group({
      username: ["", [Validators.minLength(3), Validators.maxLength(100)]],
      email: ["", [Validators.email]],
      password: ["", [Validators.minLength(8), Validators.maxLength(100)]],  // blank = keep current
    });

    this.userService.getCurrentUser().subscribe({
      next: (user) => this.form.patchValue({ username: user.username, email: user.email }),
      error: () => this.error.set("Could not load profile."),
    });
  }

  save(): void {
    if (this.form.invalid) { return; }
    this.loading.set(true);
    this.info.set(null);
    this.error.set(null);

    const v = this.form.value;
    const body: UpdateProfile = {};
    if (v.username) body.username = v.username;
    if (v.email) body.email = v.email;
    if (v.password) body.password = v.password;

    this.userService.updateProfile(body).subscribe({
      next: () => { this.loading.set(false); this.info.set("Profile updated."); },
      error: (err) => {
        this.loading.set(false);
        this.error.set(typeof err.error === "string" ? err.error : "Update failed.");
      },
    });
  }

  initials(): string{
    const name = this.userName();
    return name ? name.charAt(0).toUpperCase() : 'U';
  }

  deleteAccount(): void {
    this.userService.deleteAccount().subscribe({
      next: () => { this.authService.logout(); this.router.navigate(["/register"]); },
      error: () => this.error.set("Delete failed."),
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}