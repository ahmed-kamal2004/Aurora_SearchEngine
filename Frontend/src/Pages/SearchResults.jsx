import SearchBar from "../Components/SearchBar";
import { useEffect, useState } from "react";
import SearchResult from "../Components/SearchResult";
import LoadingWheel from "../Components/LoadingWheel";
import { useNavigate } from "react-router-dom";
import RecognitionInterface from "../Components/RecognitionInterface";
import axios from "axios";
const SearchResults = (props) => {
	const [data, setData] = useState(null);
	const history = useNavigate();
	const [isLoading, setIsLoading] = useState(true);
	const [error, setError] = useState(null);
	const [loadTime, setLoadTime] = useState(null);
	const [paging, setPaging] = useState(0);
	const [numberOfPages, setNumberOfPages] = useState(0);
	useEffect(() => {
		const fetchData = async () => {
			setIsLoading(false);
			setError(false);
			setData(null);
			setNumberOfPages(0);
			setPaging(0);
			const options = {
				method: "GET", // Or "GET" if your endpoint accepts GET requests
				url: `http://localhost:8080/ranker/search?query=${props.searchQuery}`,
				headers: {
					"Content-Type": "*",
				},
			};

			try {
				setIsLoading(true);
				const start = performance.now();
				const response = await axios.request(options);
				const end = performance.now();
				setLoadTime((end - start).toFixed(5));
				setData(response.data);
				setNumberOfPages(Object.keys(response.data).length / 20);
				setIsLoading(false);
			} catch (error) {
				setError(true);
				console.error(error);
			}
		};
		fetchData();
	}, [props.search]);

	return (
		<div className="min-h-screen relative z-10 p-8 bg-[url('..\src\assets\Bitmap.jpg')] bg-cover">
			<div className="w-[100%] h-[75px] flex gap-8 ">
				<img src="src/assets/logo.png" className=" w-[200px] h-[85px] mr-4" />
				<SearchBar
					searchQuery={props.searchQuery}
					setSearchQuery={props.setSearchQuery}
					history={history}
					search={props.search}
					setSearch={props.setSearch}
					setIsVoice={props.setIsVoice}
					isVoice={props.isVoice}
					Recognition={props.Recognition}
				/>
			</div>
			<div className="absolute top-[20%] left-0 right-0 mx-auto">
				{props.isVoice && (
					<RecognitionInterface
						setIsVoice={props.setIsVoice}
						Recognition={props.Recognition}
						history={history}
					/>
				)}
			</div>
			{isLoading && (
				<div className="absolute top-[50%] left-[50%] translate-x-[-50%] translate-y-[-50%]">
					<LoadingWheel />
				</div>
			)}
			<div className="flex flex-col justify-center mt-16">
				{error && (
					<div className="text-red-700 text-3xl text-center">
						Error: {error}
					</div>
				)}
				<div>LoadingTime:{loadTime} ms</div>
				{data &&
					Object.keys(data)
						.slice(20 * paging, 20 * (paging + 1))
						.map((key) => {
							const result = data[key];
							// console.log(result)
							return (
								<SearchResult
									key={key}
									url={result.url}
									description={result.paragraph}
									title={result.title}
									searchquery={props.searchQuery}
									search={props.search}
								/>
							);
						})}
				{data && (
					<div className="mx-auto mt-16 p-4">
						<button
							className="font-black text-xl border-2 mx-1 px-7 py-4 border-black bg-[#ffffff] text-[#232223] rounded-xl"
							onClick={() => {
								if (paging > 0) setPaging(paging - 1);
							}}>{`<`}</button>

						{Array.from({ length: numberOfPages }).map((_, i) => (
							<button
								key={i}
								className={
									paging == i
										? "font-black text-xl border-2 mx-1 px-7 py-4 border-black bg-[#ffffff] text-[#232222] rounded-xl"
										: "font-black text-xl border-2 mx-1 px-7 py-4 text-red-700 border-black bg-[rgba(255,255,255,0.66)] text-[#232222] rounded-xl"
								}
								onClick={() => setPaging(i)}>
								{i + 1}
							</button>
						))}

						<button
							className="font-black text-xl border-2 mx-1 px-7 py-4 border-black bg-[#ffffff] text-[#232222] rounded-xl"
							onClick={() => {
								if (paging < numberOfPages) setPaging(paging + 1);
							}}>{`>`}</button>
					</div>
				)}
			</div>
		</div>
	);
};
export default SearchResults;
