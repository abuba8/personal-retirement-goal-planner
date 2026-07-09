import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, Observable, throwError } from "rxjs";
import { environment } from "../../environments/environments";
import { UserProfile, UpdateProfile } from "../types/UserProfile";

@Injectable({ providedIn: "root" })
export class UserService {

  private readonly URL = `${environment.baseApiUrl}/users`;

  constructor(private http: HttpClient) {}

  getCurrentUser(): Observable<UserProfile> {
    return this.http.get<UserProfile>(this.URL)
      .pipe(catchError(() => throwError(() => new Error("Failed to load profile"))));
  }

  updateProfile(body: UpdateProfile): Observable<UserProfile> {
    return this.http.put<UserProfile>(this.URL, body)
      .pipe(catchError(() => throwError(() => new Error("Failed to update profile"))));
  }

  deleteAccount(): Observable<void> {
    return this.http.delete<void>(this.URL)
      .pipe(catchError(() => throwError(() => new Error("Failed to delete account"))));
  }
}