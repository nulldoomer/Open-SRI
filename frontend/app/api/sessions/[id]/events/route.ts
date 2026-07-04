import { NextRequest } from "next/server";

const BACKEND_URL =
  process.env.PLAYGROUND_BACKEND_URL ??
  process.env.PLAYGROUND_BACEKND_URL ??
  process.env.NEXT_PUBLIC_BACKEND_URL;

export async function GET(
    request: NextRequest,
    { params }: { params: Promise<{ id: string}> }
) {
    const { id } = await params;

    if (!BACKEND_URL) {
        return new Response("Missing backend URL", { status: 500 });
    }

    let backendResponse: Response;

    try {
        backendResponse = await fetch(`${BACKEND_URL}/sessions/${id}/events`, {
            headers: { Accept: "text/event-stream" },
        });
    } catch (error) {
        const message = error instanceof Error ? error.message : "Unknown network error";
        return new Response(message, { status: 503 });
    }

    if (!backendResponse.ok) {
        return new Response("Session not found", { status: backendResponse.status });
    }

    // Return the stream directly without buffering it in memory

    return new Response(backendResponse.body, {
        headers: {
            "Content-Type": "text/event-stream",
            "Cache-Control": "no-cache, no-transform",
            Connection: "keep-alive",
        },
    });
}
