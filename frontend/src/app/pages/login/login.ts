import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/AuthService';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
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
      identifier: ["", [Validators.required]],
      password: ["", [Validators.required]],
    });
  }

  submit(): void{
    if(this.form.invalid){return;}
    this.loading.set(true);
    this.error.set(null);

    this.authService.login(this.form.value).subscribe({
      next: () => this.router.navigate(["/goals"]),
      error: (err) => {
        this.loading.set(false);
        // wrong creds, or not verified 
        this.error.set(typeof err.error === "string" ? err.error : "Login failed");
      }
    })
  }

}
