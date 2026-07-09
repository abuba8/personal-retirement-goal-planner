export interface UserProfile{
    id: number;
    username: string;
    email: string;
}

export interface UpdateProfile{
    username?: string;
    email?: string;
    password?: string;
}