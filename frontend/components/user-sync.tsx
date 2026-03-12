"use client";

import { useEffect, useRef } from "react";
import { useUser } from "@clerk/nextjs";
import { apiPost } from "@/lib/api";

export function UserSync() {
  const { user, isLoaded } = useUser();
  const hasSynced = useRef(false);

  useEffect(() => {
    if (isLoaded && user && !hasSynced.current) {
      hasSynced.current = true;
      
      const syncUser = async () => {
        try {
          await apiPost("/api/users/sync", {
            clerkId: user.id,
            email: user.primaryEmailAddress?.emailAddress || "",
            name: user.fullName || "",
            image: user.imageUrl || "",
          });
        } catch (error) {
          console.error("Failed to sync user with backend", error);
        }
      };

      syncUser();
    }
  }, [user, isLoaded]);

  return null; // This component doesn't render anything
}
