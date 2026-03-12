"use client";

import { ClerkProvider } from "@clerk/nextjs";
import { UserSync } from "@/components/user-sync";

function AuthProvider({ children }: { children: React.ReactNode }) {
  return (
    <ClerkProvider publishableKey={process.env.NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY}>
      <UserSync />
      {children}
    </ClerkProvider>
  );
}

export default AuthProvider;