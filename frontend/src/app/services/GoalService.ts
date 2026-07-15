import { Injectable } from "@angular/core";
import { environment } from "../../environment/environments";
import { catchError, Observable, throwError } from "rxjs";
import { GoalPage } from "../types/GoalPage";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Goal } from "../types/Goal";


@Injectable({providedIn: "root"})
export class GoalService {
    private readonly URL = `${environment.baseApiUrl}/goals`;

    constructor(private http: HttpClient) {}

    // Get /goals?page=N
    getGoalsPage(page: number): Observable<GoalPage>{
        const params = new HttpParams().set("page", page);
        return this.http.get<GoalPage>(this.URL, {params})
            .pipe(catchError(() => throwError(() => new Error("Failed to load goals"))));
    }

    // Get /goals/{id}
    getGoalById(id: number): Observable<Goal> {
        return this.http.get<Goal>(this.URL + `/${id}`)
            .pipe(catchError(() => throwError(() => new Error("Failed to load goal"))));
    }

    // Post /goals
    createGoal(goal: Goal): Observable<Goal> {
        return this.http.post<Goal>(this.URL, goal)
            .pipe(catchError(() => throwError(() => new Error("Failed to create goal"))));
    }

    // Put /goals/{id}
    updateGoal(id: number, goal: Goal): Observable<Goal> {
        return this.http.put<Goal>(this.URL + `/${id}`, goal)
            .pipe(catchError(() => throwError(() => new Error("Failed to update goal"))));
    }

    // Delete /goals/{id}
    deleteGoal(id: number): Observable<void> {
        return this.http.delete<void>(this.URL + `/${id}`)
            .pipe(catchError(() => throwError(() => new Error("Failed to delete goal"))));
    }
}