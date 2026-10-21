import { useState } from "react";
import api from "./api";

export default function Auth({ onLogin }) {
  const [mode, setMode] = useState("login");
  const [form, setForm] = useState({ name: "", email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function updateForm(event) {
    setForm({ ...form, [event.target.name]: event.target.value });
  }

  async function submit(event) {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const endpoint = mode === "login" ? "/auth/login" : "/auth/register";
      const response = await api.post(endpoint, form);
      localStorage.setItem("token", response.data.token);
      onLogin(response.data.user);
    } catch (err) {
      setError(err.response?.data?.message || "Request complete nahi hua.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="auth-card">
      <p className="eyebrow">SOLE MATE ACCOUNT</p>
      <h2>{mode === "login" ? "Welcome back" : "Create account"}</h2>

      <form onSubmit={submit}>
        {mode === "register" && (
          <label>
            Name
            <input
              name="name"
              value={form.name}
              onChange={updateForm}
              required
              maxLength={120}
              autoComplete="name"
            />
          </label>
        )}

        <label>
          Email
          <input
            name="email"
            type="email"
            value={form.email}
            onChange={updateForm}
            required
            autoComplete="email"
          />
        </label>

        <label>
          Password
          <input
            name="password"
            type="password"
            value={form.password}
            onChange={updateForm}
            required
            minLength={8}
            autoComplete={mode === "login" ? "current-password" : "new-password"}
          />
        </label>

        {error && <p className="error">{error}</p>}

        <button className="button auth-submit" disabled={loading}>
          {loading ? "Please wait…" : mode === "login" ? "Login" : "Register"}
        </button>
      </form>

      <button
        className="mode-switch"
        type="button"
        onClick={() => {
          setMode(mode === "login" ? "register" : "login");
          setError("");
        }}
      >
        {mode === "login"
          ? "New here? Create an account"
          : "Already registered? Login"}
      </button>
    </section>
  );
}