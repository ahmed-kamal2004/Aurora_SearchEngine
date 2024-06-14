import RecognitionInterface from "../Components/RecognitionInterface";
import SearchBar from "../Components/SearchBar";
import AnimatedParagraph from "../Components/LoadingWheel";
import "../index.css";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
const Home = (props) => {
	const history = useNavigate();
	return (
		<div className="flex justify-start items-center flex-1 flex-col h-screen text-inherent bg-[url('..\src\assets\Background.png')] bg-cover py-48">
			<p className="typing-effect text-3xl font-mono text-[#fff9c1] text-wrap w-1">
        Welcome to Aurora , Aliens'👽 fast Engine ,Explore Internet  
			</p>
			<img src="src/assets/logo.png" className="block w-[650px] h-[300px]" />
			<SearchBar
				setIsVoice={props.setIsVoice}
				isVoice={props.isVoice}
				Recognition={props.Recognition}
				history={history}
				searchQuery={props.searchQuery}
				setSearchQuery={props.setSearchQuery}
				search={props.search}
				setSearch={props.setSearch}
			/>
			{props.isVoice && (
				<RecognitionInterface
					setIsVoice={props.setIsVoice}
					Recognition={props.Recognition}
					history={history}
				/>
			)}
		</div>
	);
};
export default Home;
