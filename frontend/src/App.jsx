import { useEffect, useState } from "react";
import api from "./api";
import Auth from "./Auth";

export default function App() {
  const [products, setProducts] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [cart, setCart] = useState({ items: [], total: 0 });

  const [user, setUser] = useState(() => {
    try {
      const saved = localStorage.getItem("user");
      return saved ? JSON.parse(saved) : null;
    } catch {
      localStorage.removeItem("user");
      return null;
    }
  });

  function handleLogin(loggedInUser) {
    localStorage.setItem("user", JSON.stringify(loggedInUser));
    setUser(loggedInUser);
  }

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
    setCart({ items: [], total: 0 });
  }

  async function loadCart() {
    try {
      const response = await api.get("/cart");
      setCart(response.data);
    } catch {
      setCart({ items: [], total: 0 });
    }
  }

  async function addToCart(productId) {
    if (!user) {
      document
        .querySelector("#account")
        ?.scrollIntoView({ behavior: "smooth" });
      return;
    }

    try {
      const response = await api.post("/cart/items", {
        productId,
        quantity: 1,
      });
      setCart(response.data);
      setError("");
    } catch (err) {
      setError(
        err.response?.data?.message || "Cart mein add nahi ho paaya."
      );
    }
  }

  async function removeFromCart(itemId) {
    try {
      const response = await api.delete(`/cart/items/${itemId}`);
      setCart(response.data);
    } catch (err) {
      setError(
        err.response?.data?.message || "Cart item remove nahi ho paaya."
      );
    }
  }

  useEffect(() => {
    api
      .get("/products")
      .then((response) => setProducts(response.data))
      .catch(() =>
        setError("Products load nahi ho paaye. Backend check karo.")
      )
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (user) {
      loadCart();
    } else {
      setCart({ items: [], total: 0 });
    }
  }, [user]);

  return (
    <main className="page">
      <header className="nav">
        <a className="brand" href="/">
          SOLE<span>MATE</span>
        </a>

        <nav className="nav-links">
          <a href="#shop">Shop</a>
          <a href="#about">About</a>

          <button
            className="cart"
            type="button"
            onClick={() =>
              document
                .querySelector("#cart")
                ?.scrollIntoView({ behavior: "smooth" })
            }
          >
            Bag{" "}
            <b>
              {cart.items.reduce((sum, item) => sum + item.quantity, 0)}
            </b>
          </button>

          {user ? (
            <button className="cart" type="button" onClick={logout}>
              Logout · {user.name}
            </button>
          ) : (
            <a href="#account">Login</a>
          )}
        </nav>
      </header>

      <section className="hero" id="shop">
        <div className="hero-copy">
          <p className="eyebrow">MOVE YOUR WAY</p>
          <h1>
            Find your
            <br />
            <em>next stride.</em>
          </h1>
          <p className="intro">
            Everyday icons and performance essentials, selected for wherever
            the day takes you.
          </p>
          <a className="button" href="#collection">
            Shop the collection <span>↗</span>
          </a>
        </div>

        <div className="hero-art" aria-label="Featured shoe">
          <div className="orb" />
          <div className="shoe">S</div>
          <span className="art-caption">
            RUN LIGHT
            <br />
            LIVE BIG
          </span>
        </div>
      </section>

      <section id="account" className="account-section">
        {!user ? (
          <Auth onLogin={handleLogin} />
        ) : (
          <p>Welcome, {user.name}! Aap login ho chuke hain.</p>
        )}
      </section>

      <section className="products" id="collection">
        <div className="section-heading">
          <div>
            <p className="eyebrow">CURATED FOR YOU</p>
            <h2>Featured shoes</h2>
          </div>
          <p>Comfort and style for every step.</p>
        </div>

        {loading && <p>Products load ho rahe hain…</p>}
        {error && <p className="error">{error}</p>}

        {!loading && !error && (
          <div className="product-grid">
            {products.map((product) => (
              <article className="product-card" key={product.id}>
                <div className="product-image">
                  <img src={product.imageUrl} alt={product.name} />
                </div>

                <p className="product-brand">{product.brand}</p>
                <h3>{product.name}</h3>

                <div className="product-bottom">
                  <strong>
                    ₹{Number(product.price).toLocaleString("en-IN")}
                  </strong>
                  <button
                    type="button"
                    className="add-button"
                    onClick={() => addToCart(product.id)}
                  >
                    Add to bag
                  </button>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>

      <section className="cart-section" id="cart">
        <p className="eyebrow">YOUR SELECTION</p>
        <h2>Shopping bag</h2>

        {!user ? (
          <p>Cart dekhne ke liye pehle login karo.</p>
        ) : cart.items.length === 0 ? (
          <p>Cart abhi khaali hai.</p>
        ) : (
          <>
            {cart.items.map((item) => (
              <article className="cart-row" key={item.id}>
                <img src={item.imageUrl} alt={item.name} />

                <div>
                  <strong>{item.name}</strong>
                  <p>
                    {item.brand} · Qty {item.quantity}
                  </p>
                </div>

                <strong>
                  ₹{Number(item.lineTotal).toLocaleString("en-IN")}
                </strong>

                <button
                  type="button"
                  onClick={() => removeFromCart(item.id)}
                >
                  Remove
                </button>
              </article>
            ))}

            <p className="cart-total">
              Total: ₹{Number(cart.total).toLocaleString("en-IN")}
            </p>
          </>
        )}
      </section>

      <footer id="about">
        <span>
          SOLE<span className="accent">MATE</span>
        </span>
        <span>Made for the miles ahead.</span>
        <span>© 2026 SoleMate</span>
      </footer>
    </main>
  );
}