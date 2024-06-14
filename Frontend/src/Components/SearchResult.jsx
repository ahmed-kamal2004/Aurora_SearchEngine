import React,{useEffect, useState} from "react";
import { Link } from "react-router-dom";
const SearchResult = ({ url, title, description, searchquery,search }) => {
	const[newDescription,setnewDescription]=useState("");
		const highlightWords = (description, searchquery) => {
		const words = description.split(/[\/\s-\+\%\C]+/);
		let wordsToHighlight = searchquery.toLowerCase().replace(/"/g, '').split(" ");

		let result = [];
		let wordIndex = 0;

		words.forEach((word, index) => {
			let isHighlighted=false;
			for(let i = 0;i<wordsToHighlight.length;i++){
				if((wordsToHighlight[i].indexOf(word.toLowerCase())!=-1 && wordsToHighlight[i].length ===word.length )||word.toLowerCase().indexOf(wordsToHighlight[i])!=-1)
					isHighlighted=true;
			}
			result.push(
				<span key={index} className={isHighlighted ? " text-xl text-[#0dff00b9] font-black" : ""}>
					{word}
				</span>
			);

			// Add space after each word except the last one
			if (wordIndex < words.length - 1) {
				result.push(" ");
			}
			wordIndex++;
		});

		return result;
	};
	useEffect(()=>{
		setnewDescription(highlightWords(description,searchquery))
	},[search])
	

	return (
		<div className="border-2 border-[#9E9E9E]  rounded-3xl p-4 mx-8 my-4 w-[75%] bg-[#413d3c] hover:bg-[#a040b410]">
			<h2 className="text-xl text-start font-serif font-normal text-nowrap  text-[#4da19bdb]">
				{url}
			</h2>
			<Link
				to={url}
				className="text-[2.25rem] text-start font-serif font-bold text-nowrap my-1 hover:text-[#d2a25e] text-[#71A0EF]">
				{title.substring(0, Math.min(80, title.length))}
			</Link>
			<h3 className="text-lg text-start font-serif font-extralight text-wrap text-[#f7f7f7] my-[5px]">
				{/* {description.substring(0, Math.min(100, description.length)) + "..."} */}
				<p>{newDescription}</p>
			</h3>
		</div>
	);
};

export default SearchResult;
