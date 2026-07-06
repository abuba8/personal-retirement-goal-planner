import { ContributionCategory } from "./enums/ContributionCategory";

export type Contribution = {
    id?: number;
    amount: number;
    date: Date;
    category: ContributionCategory;
    notes: string;
    sourceId: number;
    goalId: number;
}