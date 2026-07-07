import { Injectable } from "@angular/core";

const TOKEN_KEY = "jwt";

@Injectable({providedIn:"root"})
export class TokenService{
    // save jwt after successful login
    setToken(token: string): void{
        localStorage.setItem(TOKEN_KEY, token);
    }

    // read it back
    getToken(): string | null{
        return localStorage.getItem(TOKEN_KEY);
    }

    // remove on logout
    clear(): void{
        localStorage.removeItem(TOKEN_KEY)
    }

    // check if logged in
    isLoggedIn(): boolean{
        return this.getToken() != null;
    }
}