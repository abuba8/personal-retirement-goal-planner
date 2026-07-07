export interface RegisterRequest{
    username: string;
    email: string;
    password: string;
}

export interface LoginRequest{
    identifier: string;
    password: string;
}

export interface LoginResponse{
    token: string;
    expiresIn: number; // ms
}

export interface VerifyRequest{
    email: string;
    verificationCode: string;
}