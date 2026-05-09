'use client';

import { CalendarDays, MapPin } from 'lucide-react';

type EventItem = {
  id: string;
  title: string;
  date: string;
  time: string;
  location: string;
};

const EVENTS: EventItem[] = [
  {
    id: 'open-mic',
    title: 'Open Mic Friday',
    date: '2026-05-16',
    time: '5:00 PM',
    location: 'Main Lounge',
  },
  {
    id: 'barista-lab',
    title: 'Barista Skills Lab',
    date: '2026-05-22',
    time: '2:00 PM',
    location: 'Brewing Lab',
  },
  {
    id: 'study-night',
    title: 'Study Night',
    date: '2026-05-28',
    time: '6:30 PM',
    location: 'North Hall',
  },
];

export default function EventsPage() {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth();
  const monthLabel = today.toLocaleString('en-US', { month: 'long', year: 'numeric' });

  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const startDay = new Date(year, month, 1).getDay();
  const totalCells = Math.ceil((startDay + daysInMonth) / 7) * 7;

  const eventByDay = new Map<number, EventItem[]>();
  EVENTS.forEach((event) => {
    const eventDate = new Date(event.date);
    if (eventDate.getFullYear() === year && eventDate.getMonth() === month) {
      const day = eventDate.getDate();
      eventByDay.set(day, [...(eventByDay.get(day) || []), event]);
    }
  });

  return (
    <div className="space-y-6">
      <div className="bg-white border border-[#E5D3B3] rounded-2xl p-5 shadow-sm">
        <div className="flex items-center gap-2 mb-4">
          <CalendarDays className="w-5 h-5 text-[#001C98]" />
          <h2 className="text-lg font-bold text-[#10203B]">Events Calendar</h2>
        </div>

        <div className="flex items-center justify-between mb-4">
          <h3 className="text-base font-semibold text-[#10203B]">{monthLabel}</h3>
          <span className="text-xs text-gray-500">Events are posted by staff</span>
        </div>

        <div className="grid grid-cols-7 text-xs text-gray-500 mb-2">
          {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map((label) => (
            <div key={label} className="px-2 py-1 text-center font-semibold">
              {label}
            </div>
          ))}
        </div>

        <div className="grid grid-cols-7 gap-2">
          {Array.from({ length: totalCells }).map((_, index) => {
            const dayNumber = index - startDay + 1;
            const isValidDay = dayNumber > 0 && dayNumber <= daysInMonth;
            const dayEvents = isValidDay ? eventByDay.get(dayNumber) || [] : [];
            const isToday =
              isValidDay &&
              dayNumber === today.getDate() &&
              month === today.getMonth() &&
              year === today.getFullYear();

            return (
              <div
                key={`day-${index}`}
                className={`h-16 rounded-xl border text-xs flex flex-col items-center justify-center ${
                  isValidDay
                    ? 'border-[#E5D3B3] bg-[#FDFBF7]'
                    : 'border-transparent bg-transparent'
                } ${isToday ? 'ring-2 ring-[#001C98]/40' : ''}`}
              >
                {isValidDay && (
                  <div className="text-sm font-semibold text-[#10203B]">{dayNumber}</div>
                )}
                {dayEvents.length > 0 && (
                  <div className="mt-1 h-1.5 w-1.5 rounded-full bg-[#001C98]" />
                )}
              </div>
            );
          })}
        </div>
      </div>

      <div className="bg-white border border-[#E5D3B3] rounded-2xl p-5 shadow-sm">
        <h2 className="text-lg font-bold text-[#10203B] mb-4">Upcoming Events</h2>
        {EVENTS.length === 0 ? (
          <p className="text-sm text-gray-500">No events announced yet.</p>
        ) : (
          <div className="space-y-3">
            {EVENTS.map((event) => (
              <div key={event.id} className="border border-[#E5D3B3] rounded-xl p-3">
                <p className="font-semibold text-[#10203B]">{event.title}</p>
                <p className="text-xs text-gray-500">{event.date} at {event.time}</p>
                <p className="text-xs text-gray-500 flex items-center gap-1 mt-1">
                  <MapPin className="w-3.5 h-3.5" />
                  {event.location}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
