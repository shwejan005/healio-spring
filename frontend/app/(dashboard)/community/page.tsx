'use client';

import { useState, useEffect, useCallback } from "react";
import { useUser } from "@clerk/nextjs";
import { ThumbsUp, ThumbsDown, MessageCircle, Trash2 } from "lucide-react";
import { cn } from "@/lib/utils";
import { motion } from "framer-motion";
import { apiGet, apiPost, apiDelete } from "@/lib/api";

type Forum = {
  _id: string;
  title: string;
  content: string;
  authorId: string;
  votes: Record<string, number>;
  createdAt: number;
  author: {
    name: string;
    image?: string;
  };
  comments: {
    _id: string;
    content: string;
    author: {
      name: string;
      image?: string;
    };
  }[];
  upvotes: number;
  dislikes: number;
};

export default function ForumList() {
  const { user } = useUser();
  const [forums, setForums] = useState<Forum[] | undefined>(undefined);
  const [newPost, setNewPost] = useState({ title: "", content: "" });
  const [commentContent, setCommentContent] = useState<Record<string, string>>({});

  const fetchForums = useCallback(async () => {
    try {
      const data = await apiGet<Forum[]>("/api/forums");
      setForums(data);
    } catch (error) {
      console.error("Failed to fetch forums:", error);
    }
  }, []);

  useEffect(() => {
    fetchForums();
  }, [fetchForums]);

  const handleCreatePost = async () => {
    if (!user?.id) return;
    await apiPost("/api/forums", { clerkId: user.id, ...newPost });
    setNewPost({ title: "", content: "" });
    fetchForums();
  };

  const handleAddComment = async (forumId: string) => {
    if (!user?.id) return;
    await apiPost(`/api/forums/${forumId}/comments`, {
      clerkId: user.id,
      content: commentContent[forumId] || "",
    });
    setCommentContent({ ...commentContent, [forumId]: "" });
    fetchForums();
  };

  const handleVote = async (forumId: string, voteValue: number) => {
    if (!user?.id) return;
    await apiPost(`/api/forums/${forumId}/vote`, { clerkId: user.id, vote: voteValue });
    fetchForums();
  };

  const handleRemoveForum = async (forumId: string) => {
    if (!user?.id) return;
    await apiDelete(`/api/forums/${forumId}?clerkId=${user.id}`);
    fetchForums();
  };

  return (
    <div className="min-h-screen p-6 ">
      <div className="max-w-3xl mx-auto">
        <motion.h1
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
          className="text-3xl font-bold text-[#314328] mb-4 text-center"
        >
          Community Forum
        </motion.h1>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.3 }}
          className="mb-6 rounded-lg border bg-card p-4 shadow-md"
        >
          <h2 className="text-lg font-semibold text-[#314328] mb-2">Start a Discussion</h2>
          <input
            value={newPost.title}
            onChange={(e) => setNewPost({ ...newPost, title: e.target.value })}
            placeholder="Post title"
            className="w-full p-2 border bg-muted border-gray-300 rounded mb-2"
          />
          <textarea
            value={newPost.content}
            onChange={(e) => setNewPost({ ...newPost, content: e.target.value })}
            placeholder="Share your thoughts..."
            className="w-full p-2 border bg-muted border-gray-300 rounded h-24"
          />
          <button
            onClick={handleCreatePost}
            className="w-full bg-primary text-white px-4 py-2 rounded-md hover:bg-primary/80 transition"
          >
            Create Post
          </button>
        </motion.div>

        {forums?.map((forum) => (
          <motion.div
            key={forum._id}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3 }}
            className="mb-6 rounded-lg border bg-card p-4 shadow-md"
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <img
                  src={forum.author?.image || "https://via.placeholder.com/40"}
                  className="w-10 h-10 rounded-full"
                  alt="Author"
                />
                <div>
                  <p className="font-semibold text-[#314328]">{forum.author?.name}</p>
                  <p className="text-sm text-muted-foreground">
                    {new Date(forum.createdAt).toLocaleDateString()}
                  </p>
                </div>
              </div>
              <button
                onClick={() => handleRemoveForum(forum._id)}
                className="text-red-500 hover:text-red-700"
              >
                <Trash2 size={20} />
              </button>
            </div>

            <h3 className="text-xl font-bold mt-2 text-[#314328]">{forum.title}</h3>
            <p className="text-muted-foreground mt-2">{forum.content}</p>

            <div className="flex items-center gap-4 mt-4">
              <button
                onClick={() => handleVote(forum._id, 1)}
                className="flex items-center gap-1 text-green-600 hover:text-green-800"
              >
                <ThumbsUp size={20} /> {forum.upvotes}
              </button>
              <button
                onClick={() => handleVote(forum._id, -1)}
                className="flex items-center gap-1 text-red-600 hover:text-red-800"
              >
                <ThumbsDown size={20} /> {forum.dislikes}
              </button>
              <div className="flex items-center gap-1 text-muted-foreground">
                <MessageCircle size={20} /> {forum.comments.length}
              </div>
            </div>

            <div className="mt-4">
              <h4 className="text-md font-semibold text-[#314328]">Comments</h4>
              <div className="ml-4 border-l-2 pl-4 mt-2 border-muted">
                {forum.comments.map((comment: any) => (
                  <div key={comment._id} className="mb-3">
                    <div className="flex items-center gap-2">
                      <img
                        src={comment.author?.image || "https://via.placeholder.com/30"}
                        className="w-6 h-6 rounded-full"
                        alt="Comment author"
                      />
                      <span className="font-medium text-[#314328]">{comment.author?.name}</span>
                    </div>
                    <p className="ml-8 text-muted-foreground">{comment.content}</p>
                  </div>
                ))}
              </div>
              <div className="mt-4 flex gap-2">
                <input
                  value={commentContent[forum._id] || ""}
                  onChange={(e) => setCommentContent({
                    ...commentContent,
                    [forum._id]: e.target.value
                  })}
                  placeholder="Write a comment..."
                  className="flex-1 p-2 border bg-muted border-gray-300 rounded"
                />
                <button
                  onClick={() => handleAddComment(forum._id)}
                  className="bg-primary text-white px-4 py-2 rounded-md hover:bg-primary/80 transition"
                >
                  Comment
                </button>
              </div>
            </div>
          </motion.div>
        ))}
      </div>
    </div>
  );
}