import Link from 'next/link';
import { Loader2, type LucideIcon } from 'lucide-react';
import { cn } from '@/lib/utils';

export function PageHeader({
  title,
  subtitle,
  action,
}: {
  title: string;
  subtitle?: string;
  action?: React.ReactNode;
}) {
  return (
    <div className="flex items-start justify-between gap-4 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold text-gray-900 tracking-tight">{title}</h1>
        {subtitle && <p className="text-gray-500 text-sm mt-1">{subtitle}</p>}
      </div>
      {action}
    </div>
  );
}

export function LoadingState({ message = 'Loading...' }: { message?: string }) {
  return (
    <div className="flex flex-col items-center justify-center py-24 gap-3 animate-fade-in">
      <div className="w-12 h-12 rounded-2xl bg-[#001C98]/10 flex items-center justify-center">
        <Loader2 className="w-6 h-6 animate-spin text-[#001C98]" />
      </div>
      <p className="text-sm text-gray-500">{message}</p>
    </div>
  );
}

export function EmptyState({
  icon: Icon,
  title,
  description,
  actionLabel,
  actionHref,
}: {
  icon: LucideIcon;
  title: string;
  description?: string;
  actionLabel?: string;
  actionHref?: string;
}) {
  return (
    <div className="text-center py-16 px-6 animate-fade-in">
      <div className="w-16 h-16 rounded-2xl bg-gradient-to-br from-[#001C98]/10 to-[#E5D3B3]/40 flex items-center justify-center mx-auto mb-4 shadow-sm">
        <Icon className="w-8 h-8 text-[#001C98]/70" />
      </div>
      <p className="font-semibold text-gray-800">{title}</p>
      {description && <p className="text-sm text-gray-500 mt-1 max-w-xs mx-auto">{description}</p>}
      {actionLabel && actionHref && (
        <Link
          href={actionHref}
          className="inline-flex mt-5 px-5 py-2.5 bg-[#001C98] text-white text-sm font-semibold rounded-xl hover:bg-[#0025B8] transition shadow-md shadow-[#001C98]/20"
        >
          {actionLabel}
        </Link>
      )}
    </div>
  );
}

export function StudentCard({
  children,
  className,
  hover = false,
}: {
  children: React.ReactNode;
  className?: string;
  hover?: boolean;
}) {
  return (
    <div
      className={cn(
        'bg-white/80 backdrop-blur-sm rounded-2xl border border-[#E5D3B3]/60 shadow-sm shadow-[#001C98]/5',
        hover && 'transition hover:shadow-md hover:shadow-[#001C98]/10 hover:border-[#E5D3B3]',
        className
      )}
    >
      {children}
    </div>
  );
}
