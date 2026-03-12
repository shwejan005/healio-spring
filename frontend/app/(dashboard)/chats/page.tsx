"use client";
import React, { useState, useEffect, useRef, Suspense, FC, useCallback } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { ScrollArea } from "@/components/ui/scroll-area";
import { cn } from "@/lib/utils";
import { Send, LogOut, Plus, Users } from "lucide-react";
import { motion } from "framer-motion";
import { apiGet, apiPost } from "@/lib/api";

const ChatHeader: FC<{roomName:string;onLeave:()=>void}> = ({roomName,onLeave}) => (
  <div className="p-6 bg-[#e0f0e0] flex justify-between items-center">
    <h3 className="text-2xl font-bold text-[#2d4c2d]">{roomName}</h3>
    <Button onClick={onLeave} variant="outline" className="bg-white text-[#4a7a4a] border-[#4a7a4a] hover:bg-[#4a7a4a] hover:text-white"><LogOut className="mr-2"/>Leave Room</Button>
  </div>
);

const MessageList: FC<{messages:any[];username:string}> = ({messages,username}) => {
  const ref = useRef<HTMLDivElement>(null);
  useEffect(()=>{ref.current?.scrollIntoView({behavior:"smooth"})},[messages]);
  return (
    <ScrollArea className="flex-grow p-6">
      <div className="space-y-4 flex flex-col">
        {messages.map((msg:any,idx:number)=>(
          <motion.div key={idx} initial={{opacity:0,y:-20}} animate={{opacity:1,y:0}}>
            <div className={cn("p-4 max-w-[80%] rounded-xl shadow-md",msg.sender===username?"ml-auto bg-[#4a7a4a] text-white":"bg-[#e0f0e0] text-[#2d4c2d]")}>
              <p className="font-semibold mb-1">{msg.sender}</p><p>{msg.text}</p>
            </div>
          </motion.div>
        ))}
        <div ref={ref}/>
      </div>
    </ScrollArea>
  );
};

const ChatInput: FC<{newMessage:string;setNewMessage:(m:string)=>void;onSend:()=>void}> = ({newMessage,setNewMessage,onSend}) => (
  <div className="p-6 bg-[#e0f0e0]">
    <div className="flex items-center bg-white rounded-full overflow-hidden shadow-md">
      <Input type="text" value={newMessage} onChange={e=>setNewMessage(e.target.value)} placeholder="Type your message..." className="flex-grow border-none text-[#2d4c2d] placeholder-[#547454] text-lg py-4 px-6" onKeyDown={e=>{if(e.key==="Enter")onSend()}}/>
      <Button onClick={onSend} className="bg-[#4a7a4a] hover:bg-[#5c965c] text-white rounded-full p-4 m-2"><Send/></Button>
    </div>
  </div>
);

function ChatRoom() {
  const searchParams=useSearchParams();const router=useRouter();
  const [username,setUsername]=useState("");const [newMessage,setNewMessage]=useState("");
  const [isUsernameSet,setIsUsernameSet]=useState(false);
  const roomId=searchParams.get("room");
  const [rooms,setRooms]=useState<any[]>([]);const [messages,setMessages]=useState<any[]>([]);

  const fetchRooms=useCallback(async()=>{try{setRooms(await apiGet<any[]>("/api/rooms"))}catch(e){console.error(e)}},[]);
  const fetchMessages=useCallback(async()=>{if(!roomId)return;try{setMessages(await apiGet<any[]>(`/api/messages?roomId=${roomId}`))}catch(e){console.error(e)}},[roomId]);

  useEffect(()=>{fetchRooms()},[fetchRooms]);
  useEffect(()=>{if(roomId){fetchMessages();const i=setInterval(fetchMessages,3000);return()=>clearInterval(i)}},[roomId,fetchMessages]);
  useEffect(()=>{if(roomId&&isUsernameSet){apiPost(`/api/rooms/${roomId}/join`,{username}).catch(console.error)}},[roomId,isUsernameSet,username]);

  if(!isUsernameSet) return (
    <div className="flex items-center justify-center min-h-screen">
      <Card className="p-8 max-w-md w-full bg-white shadow-md rounded-xl">
        <h2 className="text-4xl font-bold text-center text-[#2d4c2d] mb-6">Welcome to Healio Chat</h2>
        <p className="text-xl text-center text-[#547454] mb-8">Enter your username to continue</p>
        <Input type="text" placeholder="Your username" value={username} onChange={e=>setUsername(e.target.value)} className="mb-6 border-[#4a7a4a] text-[#2d4c2d]"/>
        <Button onClick={()=>{if(username.trim())setIsUsernameSet(true)}} className="w-full bg-[#4a7a4a] hover:bg-[#5c965c] text-white font-bold py-3 px-6 rounded-full">Start Your Journey</Button>
      </Card>
    </div>
  );

  return (
    <div className="flex h-screen bg-[#f3faf3] p-6">
      <Card className="flex flex-col w-full max-w-6xl mx-auto bg-white shadow-md rounded-xl overflow-hidden">
        {!roomId?(
          <div className="p-8 flex flex-col h-full">
            <h2 className="text-3xl font-bold text-[#2d4c2d] mb-8">Healing Chat Rooms</h2>
            <Button onClick={async()=>{const r=await apiPost<any>("/api/rooms",{name:"New Room",maxUsers:5});router.push(`?room=${r.id}`)}} className="mb-8 bg-[#4a7a4a] hover:bg-[#5c965c] text-white font-bold py-3 px-6 rounded-full"><Plus className="mr-2"/>Create New Room</Button>
            <ScrollArea className="flex-grow">
              <div className="space-y-4">
                {rooms.map((room:any,i:number)=>(
                  <div key={room.id} className="p-6 bg-[#e0f0e0] rounded-xl flex justify-between items-center hover:bg-[#c8e6c8]">
                    <div><p className="text-xl font-semibold text-[#2d4c2d]">{room.name}</p><p className="text-[#547454]"><Users className="inline mr-2"/>{room.currentUsers}/{room.maxUsers} participants</p></div>
                    <Button variant="outline" onClick={()=>router.push(`?room=${room.id}`)} className="bg-white text-[#4a7a4a] border-[#4a7a4a] hover:bg-[#4a7a4a] hover:text-white">Join Room</Button>
                  </div>
                ))}
              </div>
            </ScrollArea>
          </div>
        ):(
          <>
            <ChatHeader roomName="Healing Room" onLeave={async()=>{if(roomId){await apiPost(`/api/rooms/${roomId}/leave`,{username});router.push("/chats")}}}/>
            <MessageList messages={messages} username={username}/>
            <ChatInput newMessage={newMessage} setNewMessage={setNewMessage} onSend={async()=>{if(newMessage.trim()&&roomId){await apiPost("/api/messages",{roomId:parseInt(roomId),sender:username,text:newMessage});setNewMessage("");fetchMessages()}}}/>
          </>
        )}
      </Card>
    </div>
  );
}

export default function ChatRoomWrapper(){return(<Suspense fallback={<div className="flex items-center justify-center min-h-screen">Loading Chat...</div>}><ChatRoom/></Suspense>)}
