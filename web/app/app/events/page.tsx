'use client';

import { useEffect, useState } from 'react';
import { getEvents, type Event } from '@/lib/api';
import { Calendar, CalendarDays, ExternalLink } from 'lucide-react';
import { PageHeader, LoadingState, EmptyState, StudentCard } from '@/components/student/StudentUI';

function formatDateBadge(dateStr: string) {
  const d = new Date(dateStr);
  return {
    month: d.toLocaleDateString(undefined, { month: 'short' }).toUpperCase(),
    day: d.getDate(),
    weekday: d.toLocaleDateString(undefined, { weekday: 'long' }),
  };
}

export default function EventsPage() {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getEvents().then((res) => {
      if (res.success && res.data) setEvents(res.data);
      setLoading(false);
    });
  }, []);

  if (loading) return <LoadingState message="Loading events..." />;

  return (
    <div className="space-y-5 animate-fade-in">
      <PageHeader
        title="Events"
        subtitle="Current and upcoming happenings at the lounge."
      />

      {events.length === 0 ? (
        <EmptyState
          icon={CalendarDays}
          title="No events scheduled"
          description="Check back later — something fun might be brewing."
        />
      ) : (
        <div className="space-y-3">
          {events.map((ev) => {
            const badge = formatDateBadge(ev.startDatetime);
            return (
              <StudentCard key={ev.id} hover className="p-0 overflow-hidden">
                <div className="flex">
                  <div className="w-20 shrink-0 flex flex-col items-center justify-center bg-gradient-to-b from-[#001C98]/10 to-[#001C98]/5 border-r border-[#E5D3B3]/40 py-4">
                    <span className="text-[10px] font-bold text-[#001C98]/70 tracking-wider">
                      {badge.month}
                    </span>
                    <span className="text-2xl font-extrabold text-[#001C98] leading-none mt-0.5">
                      {badge.day}
                    </span>
                  </div>
                  <div className="flex-1 p-4 min-w-0">
                    <h3 className="font-bold text-gray-900">{ev.title}</h3>
                    {ev.description && (
                      <p className="text-sm text-gray-500 mt-1 leading-relaxed">{ev.description}</p>
                    )}
                    <div className="flex items-center gap-1.5 mt-2.5">
                      <Calendar className="w-3.5 h-3.5 text-[#001C98]/60" />
                      <p className="text-xs text-[#001C98] font-semibold">
                        {badge.weekday} ·{' '}
                        {new Date(ev.startDatetime).toLocaleTimeString([], {
                          hour: '2-digit',
                          minute: '2-digit',
                        })}
                        {' – '}
                        {new Date(ev.endDatetime).toLocaleTimeString([], {
                          hour: '2-digit',
                          minute: '2-digit',
                        })}
                      </p>
                    </div>
                    {ev.postLink && (
                      <a
                        href={ev.postLink}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="inline-flex items-center gap-1 text-xs text-[#001C98] font-semibold mt-2 hover:underline"
                      >
                        <ExternalLink className="w-3 h-3" />
                        View post for more details
                      </a>
                    )}
                  </div>
                </div>
              </StudentCard>
            );
          })}
        </div>
      )}
    </div>
  );
}
