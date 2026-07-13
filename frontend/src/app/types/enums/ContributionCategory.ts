export enum ContributionCategory {
    EMPLOYEE_SALARY_DEFERRAL = "EMPLOYEE_SALARY_DEFERRAL",
    EMPLOYER_MATCH = "EMPLOYER_MATCH",
    CATCH_UP_CONTRIBUTION = "CATCH_UP_CONTRIBUTION",
    ROLLOVER = "ROLLOVER"
}

export const ContributionCategoryLabel: Record<ContributionCategory, string> = {
    [ContributionCategory.EMPLOYEE_SALARY_DEFERRAL]: 'Employee Salary Deferral',
    [ContributionCategory.EMPLOYER_MATCH]: 'Employer Match',
    [ContributionCategory.CATCH_UP_CONTRIBUTION]: 'Catch Up Contribution',
    [ContributionCategory.ROLLOVER]: 'Rollover'
}