import { ContributionCategory } from "./enums/ContributionCategory";

export type Contribution = {
    id: bigint;
    amount: number;
    date: Date;
    category: ContributionCategory;
    notes: string;
}