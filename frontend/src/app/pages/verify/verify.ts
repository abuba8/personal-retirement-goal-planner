import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/AuthService';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { MessageModule } from 'primeng/message';

@Component({
  selector: 'app-verify',
  imports: [ReactiveFormsModule, RouterLink, InputTextModule, ButtonModule, MessageModule],
  templateUrl: './verify.html',
  styleUrl: '../utils/css/auth/styles.css',
})
export class Verify {

  form!: FormGroup;
  loading = signal<boolean>(false);
  error = signal<string | null>(null);
  info = signal<string | null>(null);

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute      // lets us read the ?email= query param
  ) {}

  ngOnInit(): void {
    const email = this.route.snapshot.queryParamMap.get("email") ?? "";
    this.form = this.formBuilder.group({
      email: [email, [Validators.required, Validators.email]],
      verificationCode: ["", [Validators.required, Validators.minLength(6), Validators.maxLength(6)]],
    });
  }

  submit(): void {
    if (this.form.invalid) { return; }
    this.loading.set(true);
    this.error.set(null);

    this.authService.verify(this.form.value).subscribe({
      next: () => this.router.navigate(["/login"]),
      error: (err) => {
        this.loading.set(false);
        this.error.set(typeof err.error === "string" ? err.error : "Verification failed");
      },
    });
  }

  resend(): void {
    const email = this.form.value.email;
    if (!email) { this.error.set("Enter your email first."); return; }
    this.info.set(null);
    this.authService.resend(email).subscribe({
      next: () => this.info.set("A new code has been sent to your email."),
      error: (err) =>
        this.error.set(typeof err.error === "string" ? err.error : "Could not resend code"),
    });
  }
}