import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/AuthService';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  form!: FormGroup;
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ){}

  ngOnInit(): void{
    this.form = this.formBuilder.group({
      username: ["", [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      email: ["", Validators.required, Validators.email],
      password: ["", [Validators.required, Validators.minLength(8), Validators.maxLength(100)]],
    });
  }

  submit(): void{
    if(this.form.invalid){return;}
    this.loading.set(true);
    this.error.set(null);
    this.authService.register(this.form.value).subscribe({
      next: () => {
        this.loading.set(false);
        // account created, but not enabled. need verification
        this.router.navigate(["/verify"], {queryParams: {email: this.form.value.email}});
      },
      error: (err) => {
        this.loading.set(false);
        // backend sends the reason as plain text body
        this.error.set(typeof err.error === "string" ? err.error: "Registration failed");
      }
    })
  }
}
