import { Injectable } from "@angular/core";
import { environment } from "../../environments/environments";
import { HttpClient, HttpParams } from "@angular/common/http";
import { catchError, Observable, throwError } from "rxjs";
import { FundingSource } from "../types/FundingSource";
import { SourcePage } from "../types/pages/FundingSourcePage";

@Injectable({providedIn: "root"})
export class FundingSourceService {

    private readonly URL = `${environment.baseApiUrl}/sources`;

    constructor(private http: HttpClient) {}

    getSources(page: number): Observable<SourcePage> {

        const params = new HttpParams()
            .set("page", page);

        return this.http.get<SourcePage>(this.URL, {params})
            .pipe(
                catchError((err) => throwError(() => err)
            )
        )
    }

    getSourceById(id: number): Observable<FundingSource> {
        return this.http.get<FundingSource>(this.URL + `/${id}`)
            .pipe(
                catchError(
                    (err) => throwError(
                        () => err
                    )
                )
            )
    }

    createSource(source: FundingSource): Observable<FundingSource> {
        return this.http.post<FundingSource>(this.URL, source)
            .pipe(
                catchError(
                    (err) => throwError(
                        () => err
                    )
                )
            )
    }

    updateSource(id: number, source: FundingSource): Observable<FundingSource> {
        return this.http.put<FundingSource>(this.URL + `/${id}`, source)
            .pipe(
                catchError(
                    (err) => throwError(
                        () => err
                    )
                )
            )
    }

    deleteSource(id: number): Observable<void> {
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