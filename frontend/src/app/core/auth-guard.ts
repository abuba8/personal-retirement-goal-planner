import { inject } from "@angular/core";
import { TokenService } from "../services/TokenService"
import { CanActivateFn, Router } from "@angular/router";

export const authGuard: CanActivateFn = () => {
    const tokenService = inject(TokenService);
    const router = inject(Router);

    if (tokenService.isLoggedIn()){
        return true;
    }
    return router.createUrlTree(["/login"])
}

export const loggedInGuard: CanActivateFn = () => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  if (tokenService.isLoggedIn()) {
    return router.createUrlTree(["/dashboard"]);
  }
  return true;
};