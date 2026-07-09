export enum SourceType {
    TRADITIONAL_401K = 'TRADITIONAL_401K',
    ROTH_401K = 'ROTH_401K',
    TRADITIONAL_IRA = 'TRADITIONAL_IRA',
    ROTH_IRA = 'ROTH_IRA',
    SEP_IRA = 'SEP_IRA',
    TAXABLE_BROKERAGE = 'TAXABLE_BROKERAGE'
}

export const SourceTypeLabels: Record<SourceType, string> = {
    [SourceType.TRADITIONAL_401K]: 'Traditional 401K',
    [SourceType.ROTH_401K]: 'Roth 401K',
    [SourceType.TRADITIONAL_IRA]: 'Traditional IRA',
    [SourceType.ROTH_IRA]: 'Roth IRA',
    [SourceType.SEP_IRA]: 'SEP IRA',
    [SourceType.TAXABLE_BROKERAGE]: 'Taxable Brokerage'
}

export const SourceTypeLimit: Record<SourceType, number | null> = {
    [SourceType.TRADITIONAL_401K]: 24500,
    [SourceType.ROTH_401K]: 24500,
    [SourceType.TRADITIONAL_IRA]: 7500,         // 8600 over the age of 50 but we don't track user age?
    [SourceType.ROTH_IRA]: 7500,                // 8600 over the age of 50 but we don't track user age?
    [SourceType.SEP_IRA]: 72000,                // or 25% of the employee's compensation it takes the lower of the two?
    [SourceType.TAXABLE_BROKERAGE]: null        // no limit
}