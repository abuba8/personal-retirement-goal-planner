import { Goal } from "./Goal";

export interface GoalPage {
    content: Goal[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}