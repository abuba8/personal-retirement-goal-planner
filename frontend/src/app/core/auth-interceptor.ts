// src/app/core/auth-interceptor.ts
import { HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { TokenService } from "../services/TokenService";

// Functional interceptor: (request, next) => stream. Runs on EVERY request.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // public auth endpoints (signup/login/verify/resend) must never carry a
  // stale token from a previous session, or the backend's JwtAuthenticationFilter
  // will try (and fail) to validate it before the request reaches the controller
  if (req.url.includes("/auth/")) {
    return next(req);
  }

  const token = inject(TokenService).getToken();

  // no token -> send unchanged
  if (!token) {
    return next(req);
  }

  // requests are immutable; clone with the header added, then continue
  const authReq = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  });
  return next(authReq);
};