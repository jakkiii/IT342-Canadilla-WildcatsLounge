# 🎨 FRONTEND SETUP GUIDE - Next.js + shadcn/ui

## Complete Frontend Setup Tutorial

This guide will help you set up and run the Next.js frontend with shadcn/ui components.

---

## 📋 PREREQUISITES

Before starting, ensure you have:

### 1. Node.js (v18 or higher)
**Download:** https://nodejs.org/

1. Visit the website and download the **LTS version**
2. Run the installer
3. Follow installation prompts
4. Restart your terminal after installation

**Verify installation:**
```powershell
node --version
npm --version
```

Should show: `v18.x.x` or higher

---

## 🚀 STEP-BY-STEP SETUP

### STEP 1: Navigate to Frontend Directory

```powershell
cd z:\L13Y09W28\IT342-Canadilla-WildcatsLounge\frontend
```

### STEP 2: Install Dependencies

This will download all required packages (React, Next.js, shadcn/ui, etc.)

```powershell
npm install
```

**Expected output:**
```
added 312 packages in 45s
```

This may take 2-5 minutes on first run.

### STEP 3: Verify Environment Configuration

The `.env.local` file is already configured:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

This connects the frontend to your backend API.

### STEP 4: Start Development Server

```powershell
npm run dev
```

**Expected output:**
```
▲ Next.js 14.2.0
- Local:        http://localhost:3000
- Ready in 2.3s
```

---

## 🌐 ACCESS THE APPLICATION

### Open in Browser

1. **Home Page:** http://localhost:3000
2. **Register:** http://localhost:3000/register
3. **Login:** http://localhost:3000/login
4. **Dashboard:** http://localhost:3000/dashboard (after login)

---

## 🎯 TESTING THE FRONTEND

### Test User Registration

1. **Navigate to:** http://localhost:3000/register

2. **Fill in the form:**
   - Name: `Juan Dela Cruz`
   - Email: `juan@example.com`
   - Password: `password123`
   - Confirm Password: `password123`

3. **Click "Create account"**

4. **You should:**
   - See "Registration successful!" message
   - Be redirected to login page after 2 seconds

### Test User Login

1. **Navigate to:** http://localhost:3000/login

2. **Enter credentials:**
   - Email: `juan@example.com`
   - Password: `password123`

3. **Click "Login"**

4. **You should:**
   - Be redirected to dashboard
   - See your user information displayed

---

## 📁 PROJECT STRUCTURE

```
frontend/
├── app/                      # Next.js App Router
│   ├── page.tsx             # Home page
│   ├── layout.tsx           # Root layout
│   ├── globals.css          # Global styles
│   ├── register/
│   │   └── page.tsx         # Registration page
│   ├── login/
│   │   └── page.tsx         # Login page
│   └── dashboard/
│       └── page.tsx         # Dashboard page
│
├── components/
│   └── ui/                  # shadcn/ui components
│       ├── button.tsx       # Button component
│       ├── input.tsx        # Input component
│       ├── card.tsx         # Card component
│       └── label.tsx        # Label component
│
├── lib/
│   ├── api.ts              # API integration layer
│   └── utils.ts            # Utility functions
│
├── package.json            # Dependencies
├── tsconfig.json           # TypeScript config
├── tailwind.config.ts      # Tailwind CSS config
├── next.config.mjs         # Next.js config
└── .env.local             # Environment variables
```

---

## 🎨 FEATURES IMPLEMENTED

### 1. Home Page (`/`)
- Welcome screen
- Links to Register and Login
- Feature list

### 2. Registration Page (`/register`)
- Full name input
- Email validation
- Password validation (min 6 characters)
- Confirm password matching
- Error handling
- Success message
- Auto-redirect to login

### 3. Login Page (`/login`)
- Email input
- Password input
- Error handling
- Auto-redirect to dashboard
- Link to registration

### 4. Dashboard Page (`/dashboard`)
- Display user information
- Show name, email, registration date
- Logout functionality
- Protected route (requires login)

### 5. UI Components (shadcn/ui)
- **Button** - Primary, secondary, outline variants
- **Input** - Text, email, password fields
- **Card** - Content containers
- **Label** - Form labels
- **Icons** - Lucide React icons

---

## 🔧 DEVELOPMENT COMMANDS

### Start Development Server
```powershell
npm run dev
```
Runs on **http://localhost:3000** with hot-reload

### Build for Production
```powershell
npm run build
```
Creates optimized production build

### Start Production Server
```powershell
npm run build
npm start
```

### Lint Code
```powershell
npm run lint
```

---

## 🎭 CUSTOMIZING THE UI

### Change Color Theme

Edit `app/globals.css`:

```css
:root {
  --primary: 221.2 83.2% 53.3%;  /* Change primary color */
}
```

### Modify Components

All UI components are in `components/ui/`. You can edit:
- Button styles
- Input appearance
- Card layouts

### Add New Pages

Create a new folder in `app/`:

```
app/
└── newpage/
    └── page.tsx
```

Access at: `http://localhost:3000/newpage`

---

## 🌐 API INTEGRATION

The frontend connects to backend via `lib/api.ts`:

```typescript
// Register user
await register({ name, email, password });

// Login user
await login({ email, password });

// Check API health
await checkHealth();
```

### Change API URL

Edit `.env.local`:

```env
NEXT_PUBLIC_API_URL=http://your-api-url/api
```

---

## 🐛 TROUBLESHOOTING

### Issue 1: Port 3000 already in use

**Solution:**
```powershell
# Run on different port
npm run dev -- -p 3001
```

Access at: http://localhost:3001

### Issue 2: Module not found errors

**Solution:**
```powershell
# Delete node_modules and reinstall
Remove-Item -Recurse -Force node_modules
npm install
```

### Issue 3: Can'tconnect to backend

**Solution:**
1. Verify backend is running on port 8080
2. Check `.env.local` has correct API URL
3. Check browser console for CORS errors

### Issue 4: TypeScript errors

**Solution:**
```powershell
# Restart TypeScript server in VS Code
# Press: Ctrl+Shift+P
# Type: "TypeScript: Restart TS Server"
```

---

## 📱 RESPONSIVE DESIGN

The UI is fully responsive:
- **Mobile:** Optimized for small screens
- **Tablet:** Adapts layout
- **Desktop:** Full experience

Test by resizing your browser window!

---

## 🎨 STYLING WITH TAILWIND CSS

The project uses Tailwind CSS for styling.

### Common Utility Classes

```typescript
// Spacing
className="p-4 m-2"        // padding & margin

// Colors
className="bg-primary text-white"

// Flexbox
className="flex items-center justify-between"

// Rounded corners
className="rounded-lg"

// Shadows
className="shadow-lg"
```

---

## ✅ FRONTEND CHECKLIST

- [x] Node.js installed
- [x] Dependencies installed (`npm install`)
- [x] Development server running (`npm run dev`)
- [x] Can access home page (localhost:3000)
- [x] Registration page works
- [x] Login page works
- [x] Dashboard page works
- [x] Backend API connected
- [x] UI components styled with shadcn/ui

---

## 🚀 RUNNING FULL STACK

### Terminal 1: Backend

```powershell
cd z:\L13Y09W28\IT342-Canadilla-WildcatsLounge
mvn spring-boot:run
```

### Terminal 2: Frontend

```powershell
cd z:\L13Y09W28\IT342-Canadilla-WildcatsLounge\frontend
npm run dev
```

### Access Application

- **Frontend:** http://localhost:3000
- **Backend API:** http://localhost:8080/api

---

## 📦 DEPLOYMENT (Optional)

### Deploy to Vercel

1. Push code to GitHub
2. Visit https://vercel.com
3. Import your repository
4. Deploy automatically

### Environment Variables

Set in Vercel dashboard:
```
NEXT_PUBLIC_API_URL=https://your-backend-api.com/api
```

---

## 🎓 NEXT STEPS

After frontend setup:

1. ✅ Test all pages
2. ✅ Customize colors and styles
3. ✅ Add more features (profile page, password reset)
4. ✅ Deploy to production

---

**Frontend setup complete! Your Wildcats Lounge UI is ready! 🎉**

---

*Last Updated: March 7, 2026*
*Frontend: Next.js 14 + shadcn/ui + TypeScript*
*Project: Wildcats Lounge - Frontend Guide*
