import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Wildcats Lounge - User Management",
  description: "User Registration and Login System",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className="antialiased">{children}</body>
    </html>
  );
}
