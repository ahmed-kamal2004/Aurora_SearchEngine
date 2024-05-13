import Home from "./Pages/Home";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { useState } from "react";
import SearchResults from "./Pages/SearchResults";
export default function App() {
	const [searchQuery, setSearchQuery] = useState("");
	const [search, setSearch] = useState(0);
	const [isVoice, setIsVoice] = useState(false);
	const speechRecognition =
		window.SpeechRecognition || window.webkitSpeechRecognition;
	const Recognition = new speechRecognition();
	return (
		<Router>
			<Routes>
				<Route
					path="/"
					element={
						<Home
							searchQuery={searchQuery}
							setSearchQuery={setSearchQuery}
							search={search}
							setSearch={setSearch}
							setIsVoice={setIsVoice}
							isVoice={isVoice}
							Recognition={Recognition}
						/>
					}
				/>
				<Route
					path="/Results"
					element={
						<SearchResults
							searchQuery={searchQuery}
							setSearchQuery={setSearchQuery}
							search={search}
							setSearch={setSearch}
							setIsVoice={setIsVoice}
							isVoice={isVoice}
							Recognition={Recognition}
						/>
					}
				/>
			</Routes>
		</Router>
	);
}
