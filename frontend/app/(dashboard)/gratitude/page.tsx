"use client";

import { useState, useEffect, useCallback } from "react";
import { Card } from "@/components/ui/card";
import { useUser } from "@clerk/clerk-react";
import { CalendarIcon, TimerIcon } from "lucide-react";
import { NewEntryDialog } from "./new-entry-dialog";
import { motion } from "framer-motion";
import { apiGet, apiPost } from "@/lib/api";

export default function GratitudePage() {
  const { user } = useUser();
  const userId = user?.id || "";
  const [entries, setEntries] = useState<any[] | undefined>(undefined);

  const fetchEntries = useCallback(async () => {
    if (!userId) return;
    try {
      const data = await apiGet<any[]>(`/api/gratitude?userId=${userId}`);
      setEntries(data);
    } catch (error) {
      console.error("Failed to fetch gratitude entries:", error);
    }
  }, [userId]);

  useEffect(() => {
    fetchEntries();
  }, [fetchEntries]);

  const handleAddEntry = async (entry: { gratitude: string }) => {
    if (!userId) return;
    await apiPost("/api/gratitude", { userId, gratitude: entry.gratitude });
    fetchEntries();
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: -20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.8 }}
      className="font-montreal flex min-h-screen bg-[#E5F4DD]"
    >
      <div className="flex-1 p-8">
        <motion.div className="max-w-4xl mx-auto">
          {/* Header Section */}
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
            className="text-center mb-8"
          >
            <h1 className="text-5xl font-medium text-[#314328] mb-2">
              Reflect On The Good Things In Life
            </h1>
            <p className="text-gray-600">
              Taking a moment to express gratitude can help shift your focus to the positive and improve your well-being.
            </p>
          </motion.div>

          {/* Input Section */}
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.5 }} className="mb-6">
            <Card className="p-6 bg-white rounded-lg shadow-lg">
              <NewEntryDialog onSave={handleAddEntry} />
            </Card>
          </motion.div>

          {/* Previous Entries Section */}
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.5, delay: 0.3 }} className="mb-6">
            <Card className="p-6 bg-white rounded-lg shadow-lg">
              <h2 className="text-2xl font-medium text-[#314328] mb-4">Previous Entries</h2>
              {entries === undefined ? (
                <p>Loading entries...</p>
              ) : (
                <div className="space-y-4">
                  {entries.map((entry: any, index: number) => (
                    <motion.div
                      key={entry.id || index}
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ duration: 0.5, delay: index * 0.1 }}
                    >
                      <Card className="p-4 bg-[#F9FDF7] rounded-lg transition-transform duration-300 hover:scale-[1.02]">
                        <div className="flex items-start justify-between">
                          <div className="flex items-center gap-2">
                            <CalendarIcon className="h-5 w-5 text-gray-500" />
                            <span className="text-gray-500">
                              {new Date(entry.date).toLocaleDateString()}
                            </span>
                          </div>
                          <div className="flex items-center gap-2">
                            <TimerIcon className="h-5 w-5 text-gray-500" />
                            <span className="text-gray-500">
                              {new Date(entry.date).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit", hour12: true })}
                            </span>
                          </div>
                        </div>
                        <p className="mt-2 text-gray-700">{entry.gratitude}</p>
                      </Card>
                    </motion.div>
                  ))}
                </div>
              )}
            </Card>
          </motion.div>
        </motion.div>
      </div>
    </motion.div>
  );
}