import { NextRequest, NextResponse } from "next/server";

const BACKEND_URL = process.env.PLAYGROUND_BACKEND_URL;

export async function POST(request: NextRequest) {
    if (!BACKEND_URL) {
        return NextResponse.json(
            { error: "Missing backend URL" },
            { status: 500 }
        );
    }

    let body: unknown;

    try {
        body = await request.json();
    } catch {
        return NextResponse.json(
            { error: "Invalid JSON body" },
            { status: 400 }
        );
    }

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
            {
                error: "Failed to create session",
                details: errorBody || null,
                upstreamStatus: backendResponse.status,
            },
            { status: backendResponse.status }
        );
    }

    const responseText = await backendResponse.text();

    try {
        const data = JSON.parse(responseText);

        return NextResponse.json(data);
    } catch {
        return NextResponse.json(
            {
                error: "Backend returned non-JSON response",
                details: responseText || null,
            },
            { status: 502 }
        );
    }
}
