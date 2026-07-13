// src/app/services/AuthService.ts
import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, Observable, tap, throwError } from "rxjs";
import { environment } from "../../environments/environments";
import { TokenService } from "./TokenService";
import { RegisterRequest, LoginRequest, LoginResponse, VerifyRequest } from "../types/Auth";

@Injectable({ providedIn: "root" })
export class AuthService {

  private readonly URL = `${environment.baseApiUrl}/auth`;

  constructor(
    private http: HttpClient,
    private tokenService: TokenService
  ) {}

  // POST /auth/signup, backend returns PLAIN TEXT, so responseType:'text'
  register(body: RegisterRequest): Observable<string> {
    return this.http.post(`${this.URL}/signup`, body, { responseType: "text" })
      .pipe(catchError((err) => throwError(() => err)));
  }

  // POST /auth/verify, plain text
  verify(body: VerifyRequest): Observable<string> {
    return this.http.post(`${this.URL}/verify`, body, { responseType: "text" })
      .pipe(catchError((err) => throwError(() => err)));
  }

  // POST /auth/resend, plain text
  resend(email: string): Observable<string> {
    return this.http.post(`${this.URL}/resend`, { email }, { responseType: "text" })
      .pipe(catchError((err) => throwError(() => err)));
  }

  // POST /auth/login, returns JSON { token, expiresIn }.
  login(body: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.URL}/login`, body)
      .pipe(
        tap((res) => this.tokenService.setToken(res.token)), // tap: when the response arrives, stash the token
        catchError((err) => throwError(() => err))
      );
  }

  // logout, backend just says "ok"; the real logout is dropping the token
  logout(): void {
    this.tokenService.clear();
  }
}
