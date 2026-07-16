export interface NgoStats {
  totalKilos: number;
  uniqueDonors: number;
  efficiencyRatio: number;
  monthlyImpact: number;
  prevMonthImpact: number;
  topCategories: CategoryStat[];
  recentDonations: RecentDonation[];
}

export interface RecentDonation {
  donorName: string;
  date: string;
  quantity: number;
}

export interface CategoryStat {
  categoryName: string;
  quantity: number;
}

export interface DonorStats {
  totalKilosDonated: number;
  totalMoneyDonated: number;
  charitiesHelped: number;
  topCategories: CategoryStat[];
  estimatedMeals: number;
}

export interface DriverStats {
  totalDeliveries: number;
  totalKilosTransported: number;
  averageDistanceKm: number;
  punctualityPercentage: number;
}

export interface AdminStats {
  totalDonations: number;
  totalUsers: number;
  totalPlatformRevenue: number;
}
