import { describe, it, expect, vi, beforeEach } from "vitest";
import axios from "axios";
import { RestApiClient } from "./RestApiClient";

vi.mock("axios");

const mockedAxios = axios as unknown as {
  post: ReturnType<typeof vi.fn>;
  get: ReturnType<typeof vi.fn>;
};

describe("RestApiClient - auth", () => {
  let client: RestApiClient;

  beforeEach(() => {
    client = new RestApiClient();
    vi.clearAllMocks();
  });

  it("login should POST form-data and return user", async () => {
    const user = { id: "1", username: "john" };

    mockedAxios.post = vi.fn().mockResolvedValue({ data: user });

    const result = await client.login({
      username: "john",
      password: "secret",
    });

    expect(mockedAxios.post).toHaveBeenCalledTimes(1);

    const [url, body, config] = mockedAxios.post.mock.calls[0];

    expect(url).toBe("/api/auth/login");
    expect(body).toBeInstanceOf(FormData);
    expect(config).toEqual({
      headers: { "Content-Type": "multipart/form-data" },
    });

    expect(result).toEqual(user);
  });
});
