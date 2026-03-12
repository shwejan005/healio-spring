const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

async function getAuthToken(): Promise<string | null> {
  // In client components, we'll pass the token directly
  // This is a fallback for cases where token is not passed
  return null;
}

async function apiFetch(
  endpoint: string,
  options: RequestInit = {},
  token?: string | null
): Promise<Response> {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  return response;
}

export async function apiGet<T>(endpoint: string, token?: string | null): Promise<T> {
  const response = await apiFetch(endpoint, { method: "GET" }, token);
  if (!response.ok) {
    throw new Error(`API GET ${endpoint} failed: ${response.statusText}`);
  }
  return response.json();
}

export async function apiPost<T>(
  endpoint: string,
  body: unknown,
  token?: string | null
): Promise<T> {
  const response = await apiFetch(
    endpoint,
    {
      method: "POST",
      body: JSON.stringify(body),
    },
    token
  );
  if (!response.ok) {
    throw new Error(`API POST ${endpoint} failed: ${response.statusText}`);
  }
  return response.json();
}

export async function apiPut<T>(
  endpoint: string,
  body: unknown,
  token?: string | null
): Promise<T> {
  const response = await apiFetch(
    endpoint,
    {
      method: "PUT",
      body: JSON.stringify(body),
    },
    token
  );
  if (!response.ok) {
    throw new Error(`API PUT ${endpoint} failed: ${response.statusText}`);
  }
  return response.json();
}

export async function apiDelete(
  endpoint: string,
  token?: string | null
): Promise<void> {
  const response = await apiFetch(endpoint, { method: "DELETE" }, token);
  if (!response.ok) {
    throw new Error(`API DELETE ${endpoint} failed: ${response.statusText}`);
  }
}

export { API_BASE_URL };
