export interface NgoStats {
  totalKilos: number;
  uniqueDonors: number;
  efficiencyRatio: number;
  monthlyImpact: number;
  prevMonthImpact: number;
  totalMoney: number;
  currentMoney: number;
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
  totalKilos: number;
  totalMoney: number;
  totalDonations: number;
  topCategories: CategoryStat[];
  currentMonthImpact: number;
  prevMonthImpact: number;
  currentMoney: number;
  prevMoney: number;
  recentDonations: RecentDonation[];
}

export interface DriverStats {
  totalDeliveries: number;
  totalKilosTransported: number;
  averageDistanceKm: number;
  punctualityPercentage: number;
  activityByHour: number[];
  avgKilosPerDelivery: number;
  activeDays: number;
  monthlyPunctuality: number[];
}

export interface AdminStats {
  [key: string]: any;
}
