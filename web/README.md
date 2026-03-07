# Wildcats Lounge Frontend

Built with **Next.js 14**, **TypeScript**, and **shadcn/ui**

## Quick Start

```bash
# Install dependencies
npm install

# Run development server
npm run dev

# Open browser
http://localhost:3000
```

## Features

- ✅ User Registration with validation
- ✅ User Login with authentication
- ✅ Dashboard with user information
- ✅ Modern UI with shadcn/ui components
- ✅ Fully responsive design
- ✅ TypeScript for type safety

## Pages

- **Home** (`/`) - Landing page
- **Register** (`/register`) - User registration
- **Login** (`/login`) - User login
- **Dashboard** (`/dashboard`) - User dashboard

## Technology Stack

- **Framework:** Next.js 14 (App Router)
- **Language:** TypeScript
- **UI Components:** shadcn/ui
- **Styling:** Tailwind CSS
- **Icons:** Lucide React
- **HTTP Client:** Axios

## Environment Variables

Create `.env.local`:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

## Available Scripts

```bash
npm run dev      # Start development server
npm run build    # Build for production
npm start        # Start production server
npm run lint     # Run ESLint
```

## Project Structure

```
frontend/
├── app/                    # Next.js App Router
│   ├── page.tsx           # Home page
│   ├── layout.tsx         # Root layout
│   ├── globals.css        # Global styles
│   ├── register/          # Registration page
│   ├── login/             # Login page
│   └── dashboard/         # Dashboard page
├── components/
│   └── ui/                # shadcn/ui components
├── lib/
│   ├── api.ts             # API integration
│   └── utils.ts           # Utility functions
└── package.json           # Dependencies
```

## Documentation

See [docs/FRONTEND_SETUP.md](../docs/FRONTEND_SETUP.md) for complete setup guide.

Also see:
- [docs/COMPLETE_GUIDE.md](../docs/COMPLETE_GUIDE.md) - Full setup walkthrough
- [Backend README](../backend/README.md) - Backend API documentation

---

**Backend API must be running on port 8080**
