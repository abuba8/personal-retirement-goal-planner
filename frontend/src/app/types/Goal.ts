export interface Goal {
    id?: number;
    name: string;
    targetRetirementAge: number;
    targetAmount: number;
    notes: String | null;
}