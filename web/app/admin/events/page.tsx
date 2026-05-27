'use client';

import { useEffect, useState } from 'react';
import {
  staffGetEvents,
  staffCreateEvent,
  staffUpdateEvent,
  staffDeleteEvent,
  type Event,
} from '@/lib/api';
import { Loader2, Plus, Trash2, Calendar, ExternalLink } from 'lucide-react';

function toLocalInput(iso: string) {
  const d = new Date(iso);
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

function toApiLocalDateTime(value: string) {
  if (!value) return value;
  return value.length === 16 ? `${value}:00` : value;
}

const emptyForm = {
  title: '',
  description: '',
  postLink: '',
  startDatetime: '',
  endDatetime: '',
};

export default function AdminEventsPage() {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');

  const load = async () => {
    const res = await staffGetEvents();
    if (res.success && res.data) setEvents(res.data);
    setLoading(false);
  };

  useEffect(() => {
    load();
  }, []);

  const save = async () => {
    setError('');
    const payload = {
      title: form.title,
      description: form.description,
      postLink: form.postLink.trim() || undefined,
      startDatetime: toApiLocalDateTime(form.startDatetime),
      endDatetime: toApiLocalDateTime(form.endDatetime),
    };
    const res = editingId
      ? await staffUpdateEvent(editingId, payload)
      : await staffCreateEvent(payload);
    if (res.success) {
      setShowForm(false);
      setForm(emptyForm);
      setEditingId(null);
      load();
    } else {
      setError(res.error || 'Save failed');
    }
  };

  const edit = (ev: Event) => {
    setEditingId(ev.id);
    setForm({
      title: ev.title,
      description: ev.description || '',
      postLink: ev.postLink || '',
      startDatetime: toLocalInput(ev.startDatetime),
      endDatetime: toLocalInput(ev.endDatetime),
    });
    setShowForm(true);
  };

  const remove = async (id: number) => {
    if (!confirm('Delete this event?')) return;
    await staffDeleteEvent(id);
    load();
  };

  if (loading) {
    return (
      <div className="flex justify-center py-20">
        <Loader2 className="w-8 h-8 animate-spin text-[#001C98]" />
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Event Calendar</h1>
        <button
          onClick={() => {
            setShowForm(true);
            setEditingId(null);
            setForm(emptyForm);
          }}
          className="flex items-center gap-2 px-4 py-2 bg-[#001C98] text-white text-sm font-semibold rounded-xl"
        >
          <Plus className="w-4 h-4" /> Create Event
        </button>
      </div>

      {showForm && (
        <div className="bg-white rounded-2xl border border-[#E5D3B3] p-5 space-y-3">
          <h2 className="font-bold">{editingId ? 'Edit Event' : 'New Event'}</h2>
          {error && <p className="text-sm text-[#EF4444]">{error}</p>}
          <input
            placeholder="Event title"
            value={form.title}
            onChange={(e) => setForm({ ...form, title: e.target.value })}
            className="w-full px-3 py-2 border rounded-lg text-sm"
          />
          <textarea
            placeholder="Description"
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
            className="w-full px-3 py-2 border rounded-lg text-sm"
            rows={2}
          />
          <div>
            <label className="text-xs text-gray-500">Social media post link (optional)</label>
            <input
              type="url"
              placeholder="https://facebook.com/..."
              value={form.postLink}
              onChange={(e) => setForm({ ...form, postLink: e.target.value })}
              className="w-full px-3 py-2 border rounded-lg text-sm mt-1"
            />
          </div>
          <div className="grid sm:grid-cols-2 gap-3">
            <div>
              <label className="text-xs text-gray-500">Start</label>
              <input
                type="datetime-local"
                value={form.startDatetime}
                onChange={(e) => setForm({ ...form, startDatetime: e.target.value })}
                className="w-full px-3 py-2 border rounded-lg text-sm"
              />
            </div>
            <div>
              <label className="text-xs text-gray-500">End</label>
              <input
                type="datetime-local"
                value={form.endDatetime}
                onChange={(e) => setForm({ ...form, endDatetime: e.target.value })}
                className="w-full px-3 py-2 border rounded-lg text-sm"
              />
            </div>
          </div>
          <div className="flex gap-2">
            <button onClick={save} className="px-4 py-2 bg-[#001C98] text-white text-sm rounded-lg">
              Save
            </button>
            <button onClick={() => setShowForm(false)} className="px-4 py-2 border text-sm rounded-lg">
              Cancel
            </button>
          </div>
        </div>
      )}

      <div className="space-y-3">
        {events.map((ev) => (
          <div
            key={ev.id}
            className="bg-white rounded-2xl border border-[#E5D3B3] p-4 flex justify-between items-start"
          >
            <div className="flex gap-3">
              <Calendar className="w-5 h-5 text-[#001C98] shrink-0 mt-1" />
              <div>
                <p className="font-bold">{ev.title}</p>
                {ev.description && <p className="text-sm text-gray-500">{ev.description}</p>}
                <p className="text-xs text-[#001C98] mt-1">
                  {new Date(ev.startDatetime).toLocaleString()} –{' '}
                  {new Date(ev.endDatetime).toLocaleString()}
                </p>
                {ev.postLink && (
                  <a
                    href={ev.postLink}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="inline-flex items-center gap-1 text-xs text-[#001C98] font-semibold mt-1.5 hover:underline"
                  >
                    <ExternalLink className="w-3 h-3" />
                    View post
                  </a>
                )}
              </div>
            </div>
            <div className="flex gap-2 shrink-0">
              <button onClick={() => edit(ev)} className="text-xs text-[#001C98] font-semibold">
                Edit
              </button>
              <button onClick={() => remove(ev.id)} className="text-[#EF4444]">
                <Trash2 className="w-4 h-4" />
              </button>
            </div>
          </div>
        ))}
        {events.length === 0 && (
          <p className="text-center text-gray-500 py-8">No events yet.</p>
        )}
      </div>
    </div>
  );
}
