import { NextRequest, NextResponse } from "next/server";

const BACKEND_URL = process.env.PLAYGROUND_BACEKND_URL;

export async function POST(request: NextRequest){
    if (!BACKEND_URL) {
        return NextResponse.json(
            { error: "Missing backend URL" },
            { status: 500 }
        );
    }

    const body = await request.json();

    let backendResponse: Response;

    try {
        backendResponse = await fetch(`${BACKEND_URL}/sessions`, {
            method: "POST",
            headers: { "Content-Type" : "application/json" },
            body: JSON.stringify(body),
        });
    } catch (error) {
        const message = error instanceof Error ? error.message : "Unknown network error";
        return NextResponse.json(
            { error: "Backend unavailable", details: message },
            { status: 503 }
        );
    }

    if (!backendResponse.ok) {
        const errorBody = await backendResponse.text();
        return NextResponse.json(
            { error : "Failed to create session", details: errorBody || null },
            { status : backendResponse.status }
        );
    }

    const data = await backendResponse.json();

    return NextResponse.json(data);
}
