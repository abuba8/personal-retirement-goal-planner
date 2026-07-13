import { FundingSource } from "../FundingSource";

export interface SourcePage {
    content: FundingSource[]
    totalElements: number
    totalPages: number
    size: number
    number: number
}