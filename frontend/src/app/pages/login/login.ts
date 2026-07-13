import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/AuthService';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { MessageModule } from 'primeng/message';
import { HomePage } from '../../components/home-page/home-page';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, InputTextModule, PasswordModule, ButtonModule, MessageModule, HomePage],
  templateUrl: './login.html',
  styleUrl: '../utils/css/auth/styles.css',
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
      next: () => this.router.navigate(["/dashboard"]),
      error: (err) => {
        this.loading.set(false);
        // wrong creds, or not verified 
        this.error.set(typeof err.error === "string" ? err.error : "Login failed");
      }
    })
  }

}
