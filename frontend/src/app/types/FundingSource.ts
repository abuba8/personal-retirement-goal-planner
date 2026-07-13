import { SourceType } from "./enums/SourceType";

export type FundingSource = {
    id?: number;
    name: string;
    institution: string;
    notes: string;
    type: SourceType;
}