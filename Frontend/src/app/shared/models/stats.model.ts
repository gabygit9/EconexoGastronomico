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
  completedDonations: number;
  successRate: number;
  estimatedRations: number;
  topNgos: { ngoName: string; kilos: number }[];
  monthlyTrend: { year: number; month: number; kilos: number; money: number }[];
  funnel: any[];
  heatmap: any[];
  comparison: {
    totalKilosChangePercent: number;
    totalKilosPrev: number;
    totalMoneyChangePercent: number;
    totalMoneyPrev: number;
    totalDonationsChangePercent: number;
    totalDonationsPrev: number;
    completedDonationsChangePercent: number;
    completedDonationsPrev: number;
  } | null;
}

export interface DriverStats {
  totalDeliveries: number;
  totalKilosTransported: number;
  averageDistanceKm: number;
  punctualityPercentage: number;
  activityByHour: number[];
  avgKilosPerDelivery: number;
  activeDays: number;
  monthlyPunctuality: { month: number; value: number }[];
  funnel: any[];
  topBusinesses: { businessName: string; kilos: number }[];
  topNgos: { ngoName: string; kilos: number }[];
  monthlyTrend: { year: number; month: number; deliveries: number; kilos: number }[];
  comparison: {
    totalDeliveriesChangePercent: number;
    totalDeliveriesPrev: number;
    totalKilosChangePercent: number;
    totalKilosPrev: number;
    punctualityDeltaPoints: number;
    punctualityPrev: number;
  } | null;
}

export interface AdminStats {
  [key: string]: any;
}

export interface LandingStats {
  totalKilosDelivered: number;
  totalDeliveries: number;
  totalMoneyDonated: number;
  totalNgos: number;
  totalDonors: number;
  totalDrivers: number;
}
