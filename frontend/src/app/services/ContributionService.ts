import { Injectable } from "@angular/core";
import { environment } from "../../environment/environments";
import { HttpClient, HttpParams } from "@angular/common/http";
import { catchError, Observable, throwError } from "rxjs";
import { Contribution } from "../types/Contribution";
import { ContributionPage } from "../types/pages/ContributionPage";

@Injectable({providedIn: "root"})
export class ContributionService {

    private readonly URL = `${environment.baseApiUrl}/contributions`;

    constructor(private http: HttpClient) {}

    getContributions(page: number, goalId?: number, sourceId?: number): Observable<ContributionPage> {

        const params = new HttpParams()
            .set("goalId", goalId ?? "")
            .set("sourceId", sourceId ?? "")
            .set("page", page);

        return this.http.get<ContributionPage>(this.URL, {params})
            .pipe(
                catchError((err) => throwError(() => err)
            )
        )
    }

    getContributionById(id: number): Observable<Contribution> {
        return this.http.get<Contribution>(this.URL + `/${id}`)
            .pipe(
                catchError(
                    (err) => throwError(
                        () => err
                    )
                )
            )
    }

    createContribution(contribution: Contribution, goalId: number, sourceId: number): Observable<Contribution> {
        const params = new HttpParams().set("goalId", goalId).set("sourceId", sourceId);

        return this.http.post<Contribution>(this.URL, contribution, { params })
            .pipe(
                catchError(
                    (err) => throwError(
                        () => err
                    )
                )
            )
    }

    updateContribution(id: number, contribution: Contribution, goalId: number, sourceId: number): Observable<Contribution> {
        const params = new HttpParams()
            .set("goalId", goalId ?? "")
            .set("sourceId", sourceId ?? "");

        return this.http.put<Contribution>(this.URL + `/${id}`, contribution, { params })
            .pipe(
                catchError(
                    (err) => throwError(
                        () => err
                    )
                )
            )
    }

    deleteContribution(id: number): Observable<void> {
        return this.http.delete<void>(this.URL + `/${id}`)
            .pipe(
                catchError(
                    (err) => throwError(
                        () => err
                    )
                )
            )
    }
    
}