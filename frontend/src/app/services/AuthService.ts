// test auth to pass token and test frontend
import { Injectable, signal } from "@angular/core";

const TOKEN_KEY = "auth_token";

@Injectable({ providedIn: "root" })
export class AuthService {

    private readonly token = signal<string | null>(localStorage.getItem(TOKEN_KEY));

    getToken(): string | null {
        return this.token();
    }

    setToken(token: string): void {
        localStorage.setItem(TOKEN_KEY, token);
        this.token.set(token);
    }

    clearToken(): void {
        localStorage.removeItem(TOKEN_KEY);
        this.token.set(null);
    }
}
