'use client';

import { useRef, type ClipboardEvent, type KeyboardEvent } from 'react';
import { cn } from '@/lib/utils';

interface VerificationCodeInputProps {
  value: string;
  onChange: (value: string) => void;
  disabled?: boolean;
  length?: number;
  autoFocus?: boolean;
  className?: string;
}

export function VerificationCodeInput({
  value,
  onChange,
  disabled = false,
  length = 6,
  autoFocus = false,
  className,
}: VerificationCodeInputProps) {
  const inputsRef = useRef<Array<HTMLInputElement | null>>([]);
  const digits = Array.from({ length }, (_, index) => value[index] ?? '');

  const focusInput = (index: number) => {
    const target = inputsRef.current[index];
    if (!target) return;
    target.focus();
    target.select();
  };

  const updateDigits = (nextDigits: string[]) => {
    onChange(nextDigits.join('').slice(0, length));
  };

  const handleChange = (index: number, rawValue: string) => {
    const cleaned = rawValue.replace(/\D/g, '');
    if (!cleaned) {
      const nextDigits = [...digits];
      nextDigits[index] = '';
      updateDigits(nextDigits);
      return;
    }

    const nextDigits = [...digits];
    let cursor = index;
    for (const char of cleaned) {
      if (cursor >= length) break;
      nextDigits[cursor] = char;
      cursor += 1;
    }

    updateDigits(nextDigits);
    focusInput(Math.min(cursor, length - 1));
  };

  const handleKeyDown = (index: number, event: KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Backspace') {
      event.preventDefault();
      const nextDigits = [...digits];
      if (nextDigits[index]) {
        nextDigits[index] = '';
        updateDigits(nextDigits);
        return;
      }

      if (index > 0) {
        nextDigits[index - 1] = '';
        updateDigits(nextDigits);
        focusInput(index - 1);
      }
      return;
    }

    if (event.key === 'ArrowLeft' && index > 0) {
      event.preventDefault();
      focusInput(index - 1);
      return;
    }

    if (event.key === 'ArrowRight' && index < length - 1) {
      event.preventDefault();
      focusInput(index + 1);
    }
  };

  const handlePaste = (event: ClipboardEvent<HTMLDivElement>) => {
    event.preventDefault();
    const pasted = event.clipboardData.getData('text').replace(/\D/g, '').slice(0, length);
    if (!pasted) return;
    updateDigits(Array.from({ length }, (_, index) => pasted[index] ?? ''));
    focusInput(Math.min(pasted.length, length - 1));
  };

  return (
    <div className={cn('flex justify-center gap-3', className)} onPaste={handlePaste}>
      {digits.map((digit, index) => (
        <input
          key={index}
          ref={(node) => {
            inputsRef.current[index] = node;
          }}
          type="text"
          inputMode="numeric"
          autoComplete={index === 0 ? 'one-time-code' : 'off'}
          maxLength={1}
          value={digit}
          disabled={disabled}
          autoFocus={autoFocus && index === 0}
          onFocus={(event) => event.target.select()}
          onChange={(event) => handleChange(index, event.target.value)}
          onKeyDown={(event) => handleKeyDown(index, event)}
          className={cn(
            'h-14 w-12 rounded-2xl border-2 border-[#C9D6FF] bg-white text-center text-2xl font-bold text-gray-900 shadow-sm outline-none transition-all',
            'focus:border-[#2563EB] focus:ring-4 focus:ring-[#2563EB]/10',
            'disabled:cursor-not-allowed disabled:opacity-60'
          )}
        />
      ))}
    </div>
  );
}
