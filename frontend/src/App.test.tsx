import { render, screen} from "@testing-library/react";
import { test, expect} from "vitest"
import "@testing-library/jest-dom/vitest"
import App from "./App"

test("renders the application title", () =>{
    render(<App />);

    expect(screen.getByText("NoteCapsule")).toBeInTheDocument();
})