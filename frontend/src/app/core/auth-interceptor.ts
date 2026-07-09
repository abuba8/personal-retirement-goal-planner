// src/app/core/auth-interceptor.ts
import { HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { TokenService } from "../services/TokenService";

// Functional interceptor: (request, next) => stream. Runs on EVERY request.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(TokenService).getToken();

  // no token (e.g. the login/signup calls) -> send unchanged
  if (!token) {
    return next(req);
  }

  // requests are immutable; clone with the header added, then continue
  const authReq = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  });
  return next(authReq);
};